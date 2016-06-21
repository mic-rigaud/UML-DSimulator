package tuml.interpreter

import abcd.expression.parser.ABCDExpressionParser
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Queue
import java.util.Set
import java.util.regex.Pattern
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.OpaqueBehavior
import org.eclipse.uml2.uml.OpaqueExpression
import org.eclipse.uml2.uml.Pseudostate
import org.eclipse.uml2.uml.PseudostateKind
import org.eclipse.uml2.uml.Signal
import org.eclipse.uml2.uml.SignalEvent
import org.eclipse.uml2.uml.State
import org.eclipse.uml2.uml.StateMachine
import org.eclipse.uml2.uml.Transition
import org.eclipse.uml2.uml.UMLFactory
import org.eclipse.uml2.uml.Vertex
import org.eclipse.uml2.uml.VisibilityKind
import org.eclipse.xtend.lib.annotations.Accessors

class ActiveObject {
	val Environment env
	val Class type
	val String name
	
	val List<Vertex> states
	val Map<Vertex, Integer> state2int
	val Map<Integer, Vertex> int2state
	
	// TODO: put eventPool & currentState in Memory
	@Accessors(PUBLIC_GETTER) var Queue<Signal> eventPool = new ArrayDeque
	@Accessors(PUBLIC_GETTER) var State currentState
	
	//TODO: implement something that checks for infinite LTS due to lack of defer
	//set to -1 to disable bound checking
	public static var maxEventPoolSize = 10

	@Accessors(PUBLIC_GETTER) val peers = new HashMap<String, ActiveObject>
	@Accessors(PUBLIC_GETTER) val localVars = new HashMap<String, Object>
	val vocabulary = new HashSet<Signal>;

	new(Environment env, Class type, String name) {
		this.env = env
		this.type = type
		this.name = name
		
		//println('''create object «name»''')
		
		val region = (type.classifierBehavior as StateMachine).regions.get(0)
		this.states = region.subvertices
		//maps each state to an integer used for serialization
		int2state = new HashMap
		this.state2int = 
			states.indexed.fold(newHashMap)[map, pair | 
				map.put(pair.value, pair.key);
				int2state.put(pair.key, pair.value)
				map
			]
		
		val initialTransitions = region.subvertices.findFirst[v |
			if(v instanceof Pseudostate) {
				v.kind.value === PseudostateKind.INITIAL
			} else {
				false
			}
		].outgoings
		if(initialTransitions.size != 1) {
			throw new RuntimeException
		}
		var target = initialTransitions.get(0).target
		this.currentState = target as State

		if(env.outputPlantUMLStates) {
			println('''hnote over «name» : «currentState.name»''')
		}

		//We need to add the local vars in the hash table because they define the shape of the object
		//that cannot change dynamically
		//Normally these will be all owned attributes of the object that change during execution
		type.attributes.filter[it.visibility.value === VisibilityKind.PRIVATE.intValue].forEach[at | 
			localVars.put(at.name, false)
		]
		(type.classifierBehavior as StateMachine).regions.get(0).transitions.fold(vocabulary)[vocab, t |
			val signalz = t.triggers.map[event].filter(SignalEvent).map[signal].toSet
			vocab.addAll(signalz)
			vocab
		]
	}

	def addPeer(String name, ActiveObject peer) {
		if(!type.allAttributes().exists[a | a.name == name]) {
			throw new RuntimeException('''Class «type.name» does not have an attribute named «name»''')
		}
		peers.put(name, peer)
	}

	def getName() {
		name
	}

	def queue(Signal s) {
		eventPool.add(s)
	}
	
	val umlFactory = UMLFactory.eINSTANCE
	
	def createIgnoringTransition(Set<Signal> ignoredTriggers) {
		val ignoringTransition = umlFactory.createTransition()
		
		val triggers = ignoredTriggers.map[signal |
			
			val sE = umlFactory.createSignalEvent;
			sE.signal = signal
			val trig = umlFactory.createTrigger
			trig.event = sE
			trig
		]
		
		ignoringTransition.triggers.addAll(triggers)
		
		ignoringTransition.source = currentState
		ignoringTransition.target = currentState
		
		ignoringTransition
	}

	// activable? check UML spec
	def fireableTransitions() {
		//active triggers are needed for computing the ignored signals 
		val ignoredSignals = vocabulary.clone as Set<Signal>
		val res = currentState.outgoings.filter[t |
			if (t.triggers.empty) {
				return guard(t).eval
			}
			//remove the transition signals from the 
			val signals = t.triggers.map[event].filter(SignalEvent).map[signal].toSet
			ignoredSignals.removeAll(signals)
			
			//Question : if the guard is false should the triggers be ignored or not?
			//if the guard is not on the transitions is not fireable (blocked on the guard)
			if (!guard(t).eval) return false;
			
			//if there is no trigger the transition is fireable
			if (signals.empty) return true; 
			//if there is a trigger present and the event pool is empty the transition is not fireable (blocked on the trigger)
			if (eventPool.empty) return false
			
			val availableSignal = eventPool.peek //TODO: defer, the available signal is the first signal after the defer was processed
			//if any of the transition triggers are in the first position of the event pool the transition is fireable
			return (!eventPool.empty) && signals.contains(availableSignal)
		].map[this -> it].toSet
		//println('''«name» «res.map[p | p.key.eventPool.map[it.name]»->«(p.value.triggers.get(0).event as SignalEvent).signal.name]»''')
		
		if (!eventPool.empty && !ignoredSignals.empty) {
			res.add(this->createIgnoringTransition(ignoredSignals))
		}
		res
	}

	// TODO: support arbitrarily long whitespace
	val sendPattern = Pattern.compile("^send (\\S+) to (\\S+)$")
	val identRegex = "([A-Za-z_]\\w*)"
	val identPattern = Pattern.compile('''^«identRegex»$''')
	val assignPattern = Pattern.compile('''^«identRegex» := (.*)$''')

	def executeEffect(Transition t) {
		if(t.effect != null) {
			val body = (t.effect as OpaqueBehavior).bodies.get(0)
			val sends = body.split(";").map[stmt |
				sendPattern.matcher(stmt.trim)
			].filter[matches].map[matcher |
				matcher.group(1) -> matcher.group(2)
			]
			if(env.outputPlantUML) {
				sends.forEach[
					println('''«name» -> «it.value» : «it.key»''')
				]
			}
			sends.forEach[toSend |
				val eventPool = peers.get(toSend.value).eventPool
				if (maxEventPoolSize > 0 && eventPool.size > maxEventPoolSize) {
					throw new RuntimeException("EventPool size exceeded")
				}
				peers.get(toSend.value).queue(env.getSignal(toSend.key))
			]
			/* TODO:
				- execute assignments and sends in the specified order
			 		and not all assignments after all sends as we currently do
			*/
			//val assignments = 
			body.split(";").map[stmt |
				//println(stmt.trim)
				assignPattern.matcher(stmt.trim)
			].filter[matches].forEach[matcher |
				localVars.put(matcher.group(1), matcher.group(2).eval)
			]
		}
	}

	def guard(Transition t) {
		if (t.guard == null) {
			//here we return the string true because we eval it later with ABCD exp evaluator
			return "true"
		}
		(t.guard.specification as OpaqueExpression).bodies.get(0)
	}
	def condition(Transition t) {
		if(t.ownedRules.size != 1) {
			throw new RuntimeException
		}
		val spec = t.ownedRules.get(0).specification
		switch(spec) {
			OpaqueExpression: {
				spec.bodies.get(0)
			}
//			LiteralString: {
//				for Rhapsody?
//			}
			default: {
				throw new RuntimeException
			}
		}
	}

	val abcd = new ABCDExpressionParser()
	
	def boolean eval(String condition) {
		//println('''------->«condition»''')

/*
		// using custom parser combinator
		val parsed = ABCDExpressionLanguage.parser.parse(condition)
		val evaled = parsed.eval[localVars.get(it)]
		return evaled as Boolean
/*/
		// using PetitParser
		val evaled = abcd.eval(condition, localVars) as Boolean;
		//println('''«condition» = «evaled»''')
		return evaled as Boolean
/**/
	}

	def fire(Transition t) {
		//println('''fire «this»->«(t.triggers.get(0).event as SignalEvent).signal.name»''')
//		if(!fireableTransitions.map[value].toList.contains(t)) {
//			throw new RuntimeException
//		}

		if (!eventPool.empty && !t.triggers.empty) {
			//TODO: the remove should remove the trigger after the deferred stuff
			val s = eventPool.remove
//			if((t.triggers.get(0).event as SignalEvent).signal != s) {
//				throw new RuntimeException
//			}
		}
		
		t.executeEffect
		var target = t.target
		while(target instanceof Pseudostate) {
			switch(target.kind.value) {
				case PseudostateKind.CHOICE: {
					var tr = null as Transition
					for(e : target.outgoings.filter[it.condition != 'else']) {
						if(tr == null && e.condition.eval) {
							tr = e;
						}
					}
					if(tr == null) {
						tr = target.outgoings.findFirst[
							it.condition == 'else'
						]
					}
					tr.executeEffect
					target = tr.target
				}
				default: {
					throw new RuntimeException('''Unsupported PseudostateKind: «target»''')
				}
			}
		}
		
		currentState = target as State

		if(env.outputPlantUMLStates) {
			println('''hnote over «name» : «currentState.name»''')
		}
	}

	def toShortString() {
		'''«name»:«type.name»'''
	}

	override toString() {
		'''«name»:«type.name»(currentState = «currentState.name», eventPool = «eventPool.map[it.name]», peers = «peers.mapValues[it.name]»)'''
	}
	
	def getIntBufferSize() {
		1 + 1 + eventPool.size + localVars.size//state + eventPool size + number of signal in the eventPool * 4
	}
	
	def getBufferSize() {
		intBufferSize * 4
	}
	
	def encode(ByteBuffer buffer) {
		if (currentState instanceof Pseudostate) {
			throw new RuntimeException
		}
		//put the state
		buffer.putInt(state2int.get(currentState));
		//put the event pool size
		buffer.putInt(eventPool.size);
		//put the event pool elements
		eventPool.forEach[t | buffer.putInt(env.signal2id.get(t)) ]
		//TODO: put the localvars (serialize them) 
		localVars.forEach[k, v | 
			if (v instanceof Boolean) { 
				if ( (v as Boolean).booleanValue )
					buffer.put(1 as byte)
				else buffer.put(0 as byte) 
			}
			else if (v instanceof Integer) {
				val iv = (v as Integer).intValue
				buffer.putInt(iv)
			}
			else {
				throw new RuntimeException
			}
		]
	}
	
	def decode(ByteBuffer buffer) {
		//get the state
		val stateID = buffer.getInt;
		if (int2state.get(stateID) instanceof Pseudostate) {
			//the pseudo state is never serialized
			//println(buffer)
			//println('''error «name»''')
			throw new RuntimeException
		}
		else {
			currentState = int2state.get(stateID) as State;
		}
		//get the size of the event pool
		val epSize = buffer.getInt;
		eventPool = new ArrayDeque(epSize);
		//get the event pool signals
		for (var i = 0; i < epSize; i++) {
			eventPool.add(env.signals.get(buffer.getInt))
		}
		//TODO: get the local vars
		localVars.forEach[k, v | 
			if (v instanceof Boolean) { 
				val bv = buffer.get
				if (bv == 1)
					localVars.put(k, true)
				else 
					localVars.put(k, false)
			}
			else if (v instanceof Integer) {
				val iv = buffer.getInt
				localVars.put(k, iv)
			}
			else {
				throw new RuntimeException
			}
		]
	}
	
	def <T> accept(IAbstractVisitor<T> visitor) {
		return visitor.visitActiveObject(this);
	}
}