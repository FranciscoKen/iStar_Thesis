package validator;
import Exception.*;
import Model.*;

public class ModelValidator{
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
    }
}
