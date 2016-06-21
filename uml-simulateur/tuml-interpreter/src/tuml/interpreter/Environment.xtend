package tuml.interpreter

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.uml2.uml.Signal
import org.eclipse.xtend.lib.annotations.Data
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import java.util.Map

@Data
class Environment {
	public val outputPlantUML = false;
	public val outputPlantUMLStates = outputPlantUML && false;

	val Resource r

	val List<Signal> signalsCache = new ArrayList
	val Map<Signal, Integer> signal2idCache = new HashMap

	def signals() {
		if (signalsCache.size == 0) {
			signalsCache.addAll = r.allContents.filter(Signal).toList
		}
		signalsCache
	}

	def getSignal(String name) {
		signals.findFirst[s|s.name == name]
	}

	def signal2id() {
		if (signal2idCache.size == 0) {
			signal2idCache.putAll = signals.indexed.fold(newHashMap) [ map, pair |
				map.put(pair.value, pair.key);
				map
			]
		}
		signal2idCache
	}
}