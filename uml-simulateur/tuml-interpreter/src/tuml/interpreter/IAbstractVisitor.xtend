package tuml.interpreter

interface IAbstractVisitor<T> {
	def T visitActiveObject(ActiveObject node);
	def T visitComposite(Composite node);
}