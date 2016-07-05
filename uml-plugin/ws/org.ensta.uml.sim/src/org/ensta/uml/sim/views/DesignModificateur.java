package org.ensta.uml.sim.views;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sirius.business.api.color.AbstractColorUpdater;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
// import org.eclipse.sirius.business.internal.color.DefaultColorStyleDescription;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.FlatContainerStyle;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.SquareSpec;
import org.eclipse.sirius.diagram.description.style.StyleFactory;
import org.eclipse.sirius.diagram.description.style.impl.StyleFactoryImpl;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.RGBValues;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.ViewpointFactory;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.sirius.viewpoint.description.SystemColors;

public class DesignModificateur {

    public EList<DDiagramElement> elements;

    public DesignModificateur() {
        initialiser(SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0]);
    }

    public void initialiser(Session mysession) {
        elements = new BasicEList<DDiagramElement>();
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

    public void changeColor(String nom) {

        for (int i = 0; i < this.elements.size(); i++) {
            RGBValues newcolor;
            if (elements.get(i).getName().startsWith(nom)) {

                newcolor = VisualBindingManager.getDefault().getRGBValuesFor(SystemColors.DARK_RED_LITERAL);
                changeColor(elements.get(i).getStyle(), newcolor);
            } else {
                defaultColor(elements.get(i).getStyle());

            }

        }
    }

    protected void defaultColor(Style style) {
        StyleFactory factory = new StyleFactoryImpl();
        RGBValues color = null;
        AbstractColorUpdater update = new AbstractColorUpdater();
        System.out.println(style.getDescription().toString());
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

    public void setSession(URI path) {
        initialiser(SessionManager.INSTANCE.getExistingSession(path));
    }

    public void getModel() {
        Session mysession = SessionManager.INSTANCE.getSessions().toArray(new Session[0])[0];
        if (mysession != null) {

            Set<Resource> res = mysession.getAllSessionResources();

            for (Resource ressource : res) {
                System.out.println("toto " + ressource.toString());
            }
        }
    }

}
