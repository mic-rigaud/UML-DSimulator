package org.ensta.uml.sim.views;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
// import org.eclipse.sirius.business.internal.color.DefaultColorStyleDescription;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.FlatContainerStyle;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeListSpec;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.SquareSpec;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.RGBValues;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.ViewpointFactory;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.sirius.viewpoint.description.SystemColors;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.StateMachine;

public class DesignModificateur {

    public EList<DDiagramElement> elements;

    public HashMap<String, String> activeState;

    private String currentClasse;

    public DesignModificateur() {
        initialiser(SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0]);
    }

    public void initialiser(Session mysession) {
        elements = new BasicEList<DDiagramElement>();
        activeState = new HashMap<String, String>();
        currentClasse = "";
        if (mysession != null) {
            final Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(mysession);
            for (final DRepresentation representation : representations) {

                if (representation instanceof DSemanticDiagram) {
                    DSemanticDiagram diagram = (DSemanticDiagram) representation;
                    for (int i = 0; i < diagram.getDiagramElements().size(); i++) {
                        this.elements.add(diagram.getDiagramElements().get(i));
                    }
                }
            }
        }
    }

    public void refreshElements(String currentClasse, HashMap<String, String> hash) {
        this.currentClasse = currentClasse.toLowerCase();
        this.activeState.clear();
        this.activeState = hash;
    }

    public void refreshColor() {
        for (int i = 0; i < this.elements.size(); i++) {
            RGBValues newcolor;
            if (!currentClasse.isEmpty() && elements.get(i).getName().toLowerCase().startsWith(currentClasse)) {
                newcolor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_RED_LITERAL);
                changeColor(elements.get(i).getStyle(), newcolor);
            } else if (isInActiveState(elements.get(i))) {
                newcolor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_CHOCOLATE_LITERAL);
                changeColor(elements.get(i).getStyle(), newcolor);
            } else {
                defaultColor(elements.get(i).getStyle());
            }
        }
    }

    private boolean isInActiveState(DDiagramElement dDiagramElement) {
        if (dDiagramElement.eContainer().eContainer().eContainer() instanceof DSemanticDiagram) {
            DSemanticDiagram semantic = (DSemanticDiagram) dDiagramElement.eContainer().eContainer().eContainer();
            if (semantic.getTarget() instanceof StateMachine) {
                StateMachine sm = (StateMachine) semantic.getTarget();
                if (sm.eContainer() instanceof Class) {
                    String nom = ((Class) sm.eContainer()).getName().toLowerCase();
                    if (activeState.containsKey(nom)) {
                        if (activeState.get(nom).equalsIgnoreCase(dDiagramElement.getName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected void defaultColor(Style style) {
        RGBValues color = null;
        if (style instanceof FlatContainerStyle) {
            color = (RGBValues) ViewpointFactory.eINSTANCE.createFromString(ViewpointPackage.eINSTANCE.getRGBValues(), "209,209,209");
        } else if (style instanceof SquareSpec) {
            color = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.BLACK_LITERAL);
        } else {
            color = (RGBValues) ViewpointFactory.eINSTANCE.createFromString(ViewpointPackage.eINSTANCE.getRGBValues(), "209,209,209");
        }
        changeColor(style, color);

    }

    protected void changeColor(Style style, RGBValues newcolor) {
        if (style instanceof FlatContainerStyle) {
            FlatContainerStyle sd = (FlatContainerStyle) style;
            TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sd);
            domain.getCommandStack().execute(new RecordingCommand(domain) {
                @Override
                protected void doExecute() {

                    sd.setForegroundColor(newcolor);
                }
            });

        } else if (style instanceof SquareSpec) {
            SquareSpec sd = (SquareSpec) style;
            TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sd);
            domain.getCommandStack().execute(new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    sd.setLabelColor(newcolor);
                }
            });

        }
    }

    /*****************************************************************************
     * les fonctions suivantes servent a du debug
     */
    public void printInfo() {
        System.out.println("-------");
        for (int i = 0; i < this.elements.size(); i++) {
            DDiagramElement element = elements.get(i);
            if (element instanceof DNodeListSpec) {
                DNodeListSpec dnode = (DNodeListSpec) element;
                System.out.println(element.getName() + " : " + dnode.toString());
            }
        }
    }

    public void printElements() {
        for (int i = 0; i < this.elements.size(); i++) {
            System.out.println("element " + i + " : " + elements.get(i).getName());
        }
    }

    public String elementsToString() {
        String phrase = "";
        for (int i = 0; i < this.elements.size(); i++) {
            phrase += "element " + i + " : " + elements.get(i).getName() + "\n";
        }
        return phrase;
    }

}
