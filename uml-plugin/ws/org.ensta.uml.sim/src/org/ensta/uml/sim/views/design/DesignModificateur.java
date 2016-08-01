package org.ensta.uml.sim.views.design;

import java.util.Collection;

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
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.SquareSpec;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.RGBValues;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.description.SystemColors;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.StateMachine;
import org.ensta.uml.sim.views.model.StateModel;

import json.JSONObject;

public class DesignModificateur {

    private EList<DDiagramElement> elements;

    public DesignModificateur() {
        elements = new BasicEList<DDiagramElement>();
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

    public void refreshColor() {
        for (int i = 0; i < this.elements.size(); i++) {
            RGBValues backgroundColor, borderColor;
            borderColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.RED_LITERAL);
            if (!StateModel.getCurrentClasse().isEmpty() && elements.get(i).getName().toLowerCase().startsWith(StateModel.getCurrentClasse())) {
                backgroundColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_RED_LITERAL);
                changeColor(elements.get(i).getStyle(), backgroundColor, borderColor);
            } else if (isInActiveState(elements.get(i))) {
                backgroundColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_RED_LITERAL);
                changeColor(elements.get(i).getStyle(), backgroundColor, borderColor);
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
                    for (Object obj : StateModel.getCurrentState()) {
                        JSONObject jsonClasse = (JSONObject) obj;
                        if (jsonClasse.getString("class").equals(classe)) {
                            for (Object obj2 : jsonClasse.getJSONArray("instance")) {
                                JSONObject jsonInstance = (JSONObject) obj2;
                                if (jsonInstance.getString("name").equals(StateModel.getCurrentInstances(classe)) || StateModel.getCurrentInstances(classe).equals("all")) {
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
        RGBValues backgroundColor = null;
        RGBValues borderColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.BLACK_LITERAL);
        if (style instanceof FlatContainerStyle) {
            backgroundColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.WHITE_LITERAL);
        } else if (style instanceof SquareSpec) {
            backgroundColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.BLACK_LITERAL);
        } else {
            backgroundColor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.WHITE_LITERAL);
        }
        changeColor(style, backgroundColor, borderColor);

    }

    private void changeColor(Style style, RGBValues backgroundcolor, RGBValues borderColor) {
        if (style instanceof FlatContainerStyle) {
            FlatContainerStyle sd = (FlatContainerStyle) style;
            TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sd);
            domain.getCommandStack().execute(new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    sd.setBackgroundColor(backgroundcolor);
                    sd.setBorderColor(borderColor);
                }
            });

        } else if (style instanceof SquareSpec) {
            SquareSpec sd = (SquareSpec) style;
            TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(sd);
            domain.getCommandStack().execute(new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    sd.setLabelColor(backgroundcolor);
                }
            });

        }
    }

}
