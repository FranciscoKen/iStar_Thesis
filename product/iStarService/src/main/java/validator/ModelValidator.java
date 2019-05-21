package validator;
import Exception.*;
import Model.*;

import java.util.ArrayList;
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
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.isaWrongActorElementException +"[Element "+al.getFrom()+" and "+al.getTo()+"]");
                }
            }
        }

        //validate ielementLink
        for(Map.Entry<HashMap<String,String>,IntentionalElementLink> entry : model.getiElementLinks().entrySet()){
            if(!model.getiElements().containsKey(entry.getKey().entrySet().iterator().next().getKey()) ||
                    !model.getiElements().containsKey(entry.getKey().entrySet().iterator().next().getValue())){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.ielementLinkINonexistingiElementException);
            }
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
                                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.RESOURCE) ||
                                        model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.QUALITY)
                        )){

                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.contributionElementTypeException);
                }
            } else if(entry.getValue().getType().equals(IntentionalElementLinkType.NEEDEDBY)){
                if(model.getiElements().get(entry.getKey().entrySet().iterator().next().getKey()).getType().equals(IntentionalElementType.RESOURCE) &&
                model.getiElements().get(entry.getKey().entrySet().iterator().next().getValue()).getType().equals(IntentionalElementType.TASK)){

                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.neededByElementTypeException);
                }
            }
        }

        for(Map.Entry<String,Dependency> entry : model.getDependencies().entrySet()){
            //checks for the availability of actors
            if(! (model.getActors().containsKey(entry.getValue().getDependee()) && model.getActors().containsKey(entry.getValue().getDepender()))){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Dependency between actor "+entry.getValue().getDependee()+" and "+entry.getValue().getDepender()+"]");
            }
            //checks for the availability of intentional element
            if(!entry.getValue().getDependeeElement().equals("")){
                if(!model.getiElements().containsKey(entry.getValue().getDependeeElement())){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Intentional Element "+entry.getValue().getDependeeElement()+"]");
                }
            }

            //checks that dependerelmt / dependeelemt is indeed from the depender/dependee
            if(model.getiElements().containsKey(entry.getValue().getDependeeElement()) &&
                    model.getiElements().containsKey(entry.getValue().getDependerElement())){
                if(model.getActors().containsKey(entry.getValue().getDependee()) &&
                    model.getActors().containsKey(entry.getValue().getDepender())){
                    if(!model.getiElements().get(entry.getValue().getDependeeElement()).getActorID().equals(entry.getValue().getDependee())){
                        throw new IStarException(ExceptionMessages.heading+ExceptionMessages.dependencyElementNotFromDependencyActorException+"[Dependee Element "+model.getiElements().get(entry.getValue().getDependeeElement()).getName()+"]");
                    }
                    if(!model.getiElements().get(entry.getValue().getDependerElement()).getActorID().equals(entry.getValue().getDepender())){
                        throw new IStarException(ExceptionMessages.heading+ExceptionMessages.dependencyElementNotFromDependencyActorException+"[Depender Element "+model.getiElements().get(entry.getValue().getDependerElement()).getName()+"]");
                    }
                } else {
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+" [Depender / Dependee from dependency "+entry.getValue().getDependum().getName()+"]");
                }
            } else {
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+" [Depender / Dependee Element from dependency "+entry.getValue().getDependum().getName()+"]");
            }

            //checks for cyclical dependency
            if(entry.getValue().getDependee().equals(entry.getValue().getDepender())){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.cyclicalDependencyException+"[Dependency between "+model.getActors().get(entry.getValue().getDependee()).getName()+" and "+model.getActors().get(entry.getValue().getDepender()).getName()+"]");
            }

            if(!entry.getValue().getDependerElement().equals("")){
                if(!model.getiElements().containsKey(entry.getValue().getDependerElement())){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.referencedElementNotFoundException+"[Intentional Element "+entry.getValue().getDependerElement());
                }
                try {
                    checkIfIElementRefinedOrContributed(model,entry.getValue().getDependerElement());
                } catch (IStarException iex){
                    throw new IStarException(iex.getMessage());
                }

            }
        }


        //TODO implement ielement link check if refinement only and / or
        ArrayList<String> iElementToSets = getIElementToSet(model);

        for(String to : iElementToSets){
            int andCount = 0;
            int orCount = 0;
            for(Map.Entry<HashMap<String,String>,IntentionalElementLink> entry : model.getiElementLinks().entrySet()){
                if(entry.getKey().entrySet().iterator().next().getValue().equals(to)){
                    if(entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND)){
                        andCount++;
                    } else if(entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                        orCount++;
                    }
                }
            }
            if(andCount>0 && orCount>0){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.refinementTypeNotConsistentException);
            }

            if(andCount ==1 && orCount==0){
                throw new IStarException(ExceptionMessages.heading+ExceptionMessages.andRefinementLinkCannotBeSingle);
            }
        }
    }

    private void checkIfIElementRefinedOrContributed(IStarModel model, String iElementID) throws IStarException{

        for(Map.Entry<HashMap<String,String>,IntentionalElementLink> entry : model.getiElementLinks().entrySet()){
            if(entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                if(entry.getKey().entrySet().iterator().next().getValue().equals(iElementID)){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.dependendencyDependerElementRefinedException);
                }
            }
            if(entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_MAKE) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HELP) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_HURT) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.CONTRIBUTION_BREAK)){
                if(entry.getKey().entrySet().iterator().next().getValue().equals(iElementID)){
                    throw new IStarException(ExceptionMessages.heading+ExceptionMessages.dependendencyDependerElementContributedException);
                }
            }
        }
    }

    private ArrayList<String> getIElementToSet(IStarModel model){
        ArrayList<String> uniqueSet = new ArrayList<>();
        for(Map.Entry<HashMap<String,String>,IntentionalElementLink> entry : model.getiElementLinks().entrySet()){
            if(entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_AND) ||
                    entry.getValue().getType().equals(IntentionalElementLinkType.REFINEMENT_OR)){
                if(uniqueSet.contains(entry.getKey().entrySet().iterator().next().getValue())){

                } else {
                    uniqueSet.add(entry.getKey().entrySet().iterator().next().getValue());
                }
            }
        }
        return uniqueSet;
    }
}
