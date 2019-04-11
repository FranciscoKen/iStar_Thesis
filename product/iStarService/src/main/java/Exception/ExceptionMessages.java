package Exception;

public class ExceptionMessages {
    public static final String heading = "ISTAR ERROR : ";
    public static final String sameIDException = "Element ID must be unique";
    public static final String falseSchemaException = "Model doesn't comply to the iStarML XSD";
    public static final String falseFileTypeException = "Istar Model should be kept in .istarml2 file";
    public static final String emptyFileException = "File should not be empty";
    public static final String classResourceNotExistException = "Class Resource does not exist. Please make sure that you are using the right UID or please make another class diagram request";
    public static final String referencedElementNotFoundException ="Element in which you referenced does not exist";


    public static final String isaWrongActorElement = "Actor Element Link with type is-a should be between actors and roles";
    public ExceptionMessages(){

    }

}
