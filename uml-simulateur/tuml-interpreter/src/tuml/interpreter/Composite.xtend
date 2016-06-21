package tuml.interpreter

import java.util.HashMap
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.Element
import org.eclipse.uml2.uml.Property
import org.eclipse.uml2.uml.Type
import java.nio.ByteBuffer
import org.eclipse.xtend.lib.annotations.Accessors

class Composite {
	val Environment env
	@Accessors(PUBLIC_GETTER) val Class c
	val parts = new HashMap<String, ActiveObject>

	new(Environment env, Class c) {
		this.env = env
		this.c = c
		c.ownedAttributes.filter[isProcessInstance].forEach[part |
			parts.put(part.name, new ActiveObject(env, part.type as Class, part.name))
		]
		c.ownedConnectors.forEach[con |
			val end1 = con.ends.get(0)
			val ao1 = parts.get(end1.role.name)
			val end2 = con.ends.get(1)
			val ao2 = parts.get(end2.role.name)
			if(end2.definingEnd.class_ != null) {
				ao1.addPeer(end2.definingEnd.name, ao2)
			}
			if(end1.definingEnd.class_ != null) {
				ao2.addPeer(end1.definingEnd.name, ao1)
			}
		]
	}

	def getEnvironment() {
		return env;
	}

	def fireableTransitions() {
		parts.values.map[it.fireableTransitions].flatten.toList
	}

	def getPart(String partName) {
		parts.get(partName)
	}

	def getParts() {
		parts.values
	}

	override toString() {
		parts.values.map[it.toString].fold("")[acc, e |
			acc + e + "\n"
		]
	}

	def static isProcess(Type c) {
		if(c instanceof Class) {
			c.isActive
		} else {
			false
		}
	}

	def static isComponent(Element e) {
		if(e instanceof Class) {
			!e.ownedConnectors.empty
		} else {
			false
		}
	}

	def static isProcessInstance(Property p) {
		p.type != null && p.type.isProcess && p.owner.isComponent
	}
	
	def getBufferSize() {
		parts.values.fold(0)[size, ao | size + ao.getBufferSize]
	}
	
	def encode(ByteBuffer buffer) {
		parts.values.forEach[ao| ao.encode(buffer)]
		//I flip the buffer to allow reading from it later
		buffer.flip
	}
	
	def decode(ByteBuffer buffer) {
		parts.values.forEach[ao| ao.decode(buffer)]
		//I flip the buffer to allow reading again from it
		buffer.flip
	}
	
	def getCurrentState() {
		var buffer = ByteBuffer.allocate(this.bufferSize);
		this.encode(buffer);
		return buffer;
	}
	
	def <T> accept(IAbstractVisitor<T> visitor) {
		return visitor.visitComposite(this);
	}
}