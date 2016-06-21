package tuml.interpreter;

import tuml.interpreter.ActiveObject;
import tuml.interpreter.Composite;

@SuppressWarnings("all")
public interface IAbstractVisitor<T extends Object> {
  public abstract T visitActiveObject(final ActiveObject node);
  
  public abstract T visitComposite(final Composite node);
}
