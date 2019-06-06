package Exception;

public class ExceptionMessages {
    public static final String heading = "ISTAR ERROR : ";
    public static final String sameIDException = "Element ID must be unique";
    public static final String falseSchemaException = "Model doesn't comply to the iStarML XSD";
    public static final String falseFileTypeException = "Istar Model should be saved in .istarml2 file";
    public static final String emptyFileException = "File should not be empty";
    public static final String classResourceNotExistException = "Class Resource does not exist. Please make sure that you are using the right UID or please make another class diagram request";
    public static final String referencedElementNotFoundException ="Element in which you referenced does not exist";

    public static final String refinementValueException = "Intentional Element Link refinement only consists of AND and OR value";
    public static final String refinementElementTypeException = "Intentional Element Link and refinement should be connecting intentional element with type task and goal";

    public static final String refinementTypeNotConsistentException="Refinement Value towards an element should only be a single type";
    public static final String andRefinementLinkCannotBeSingle = "And Refinement Link should minimal be 2";

    public static final String contributionValueException ="Intentional Element Link contribution only consists of MAKE, HELP, HURT, and BREAK value";
    public static final String contributionElementTypeException = "Intentional Element Link contribution should only be connecting between any intentional element to a quality";

    public static final String qualificationElementTypeException = "Intentional Element Link qualification should only connect quality element to a goal, task, or resource";

    public static final String neededByElementTypeException = "Intentional Element Link Needed by should only connect task and resource";
    public static final String isaWrongActorElementException = "Actor Element Link with type is-a should be between actors and roles";

    public static final String dependendencyDependerElementRefinedException = "Depender Element in Dependency should not be refined (child of a refinement link)";
    public static final String dependendencyDependerElementContributedException = "Depender Element in Dependency should not be contributed (child of a contribution link)";

    public static final String cyclicalDependencyException = "Depender and dependee should not be from the same actor";
    public static final String dependencyElementNotFromDependencyActorException = "Depender / dependee Element should come from depender / dependee";

    public static final String ielementLinkINonexistingiElementException = "Intentional element not found. Intentional element link should only connects an intentional element with other intentional element";

    public static final String multipleActorLinksBetweenTwoActors = "There should only be one actor link betweem two actors";

    public ExceptionMessages(){

    }

}
