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

import json.JSONArray;
import json.JSONObject;

public class DesignModificateur {

    public EList<DDiagramElement> elements;

    private String currentClasse;

    private JSONArray currentState;

    private HashMap<String, String> currentInstances;

    public DesignModificateur() {
        elements = new BasicEList<DDiagramElement>();
        currentState = new JSONArray();
        currentClasse = "";
        currentInstances = new HashMap<String, String>();
    }

    public DesignModificateur(Session mysession) {
        this();
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

    public void refreshElements(String currentClasse, JSONArray currentState) {
        this.currentClasse = currentClasse.toLowerCase();
        this.currentState = currentState;
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
                    String classe = ((Class) sm.eContainer()).getName().toLowerCase();
                    String state = dDiagramElement.getName().toLowerCase();
                    for (Object obj : currentState) {
                        JSONObject jsonClasse = (JSONObject) obj;
                        if (jsonClasse.getString("class").equals(classe)) {
                            for (Object obj2 : jsonClasse.getJSONArray("instance")) {
                                JSONObject jsonInstance = (JSONObject) obj2;
                                if (jsonInstance.getString("name").equals(currentInstances.get(classe)) || currentInstances.get(classe).equals("all")) {
                                    for (Object obj3 : jsonInstance.getJSONArray("state")) {
                                        if (obj3 instanceof String) {
                                            if (((String) obj3).equalsIgnoreCase(state))
                                                return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void defaultColor(Style style) {
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

    private void changeColor(Style style, RGBValues newcolor) {
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

    public JSONArray getCurrentState() {
        return this.currentState;
    }

    public void putCurrentInstances(String className, String instanceName) {
        this.currentInstances.put(className, instanceName);
    }

    public void putCurrentInstances(String className) {
        if (!this.currentInstances.containsKey(className))
            this.currentInstances.put(className, "all");
    }

    public boolean isCurrentInstancesContains(String className, String instanceName) {
        if (this.currentInstances.containsKey(className)) {
            if (this.currentInstances.get(className).equalsIgnoreCase(instanceName))
                return true;
        }
        return false;
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
