package validator;
import Exception.*;
import Model.*;

import java.util.HashMap;
import java.util.Map;

public class ModelValidator{
    private String filePath;

    public ModelValidator(){
    }

    public void validateModel(IStarModel model) throws IStarException{
        for(ActorLink al : model.getActorLinks()){
            if(!model.getActors().containsKey(al.getFrom())){
                throw new IStarException(ExceptionMessages.referencedElementNotFoundException +"[Element"+al.getFrom()+"]");
            }
            if(!model.getActors().containsKey(al.getTo())){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException +"[Element"+al.getTo()+"]");
            }
            if(al.getFrom().equals(al.getTo())){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.sameIDException+"[Element "+al.getFrom()+"]");
            }
            if(al.getType().equals(ActorLinkType.ISA)){
                if(!(model.getActors().get(al.getFrom()).getType().equals(model.getActors().get(al.getTo()).getType())
                && (model.getActors().get(al.getFrom()).getType().equals(ActorType.ROLE) || model.getActors().get(al.getFrom()).getType().equals(ActorType.ACTOR)))){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.isaWrongActorElement +"[Element "+al.getFrom()+" and "+al.getTo());
                }
            }
        }

        //validate ielementLink
        for(Map.Entry<HashMap<String,String>,IntentionalElementLink> entry : model.getiElementLinks().entrySet()){
            if(entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                // intentional element link for refinement should be between task and goal
                if(model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.TASK) ||
                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.TASK) ||
                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.GOAL) ||
                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.GOAL)){
                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.refinementElementTypeException);

                }
            } else if(entry.getValue().getType().equals(IntentionalElementLinkType.QUALIFICATION)){
                if(model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.QUALITY) &&
                        (
                            model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.TASK) ||
                                    model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.GOAL) ||
                                    model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.RESOURCE)
                        )){

                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.qualificationElementTypeException);
                }
            } else if(entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HELP)||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_MAKE) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HURT) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_BREAK)){
                if(model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.QUALITY) &&
                        (
                                model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.TASK) ||
                                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.GOAL) ||
                                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.RESOURCE)
                        )){

                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.contributionElementTypeException);
                }
            } else if(entry.getValue().getType().equals(IntentionalElementLinkType.NEEDEDBY)){
                if(model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.RESOURCE) &&
                model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.TASK)){

                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.neededByElementTypeException);
                }
            }
        }

        for(Map.Entry<String,Dependency> entry : model.getDependencies().entrySet()){
            //checks for the availability of actors
            if(! (model.getDependencies().containsKey(entry.getValue().getDependee()) && model.getDependencies().containsKey(entry.getValue().getDepender()))){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Dependency between actor "+entry.getValue().getDependee()+" and "+entry.getValue().getDepender()+"]");
            }
            //checks for the availability of intentional element
            if(!entry.getValue().getDependeeElement().equals("")){
                if(!model.getiElements().containsKey(entry.getValue().getDependeeElement())){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Intentional Element "+entry.getValue().getDependeeElement());
                }
            }

            if(!entry.getValue().getDependerElement().equals("")){
                if(!model.getiElements().containsKey(entry.getValue().getDependerElement())){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Intentional Element "+entry.getValue().getDependerElement());
                }
                if(isIElementContributed(model,entry.getValue().getDependerElement())){
                    
                }
                if(isIElementRefined(model,entry.getValue().getDependerElement())){

                }
            }
        }

        //TODO implement dependency's depender is not refined and contributed

        //TODO implement ielement link check if refinement only and / or
    }

    private boolean isIElementRefined(IStarModel model, String iElementID){
        boolean refined = false;

        return refined;
    }

    private boolean isIElementContributed(IStarModel model, String iElementID){
        boolean contributed = false;

        return contributed;
    }
}
