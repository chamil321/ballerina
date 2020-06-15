// This is an empty Ballerina object autogenerated to represent the `java.lang.Class` Java class.
//
// If you need the implementation of this class generated, please use the following command.
//
// $ ballerina bindgen [(-cp|--classpath) <classpath>...] [(-o|--output) <output>] (<class-name>...)
//
// E.g. $ ballerina bindgen java.lang.Class



# Ballerina object mapping for Java class `java/lang/Class`.
#
# + _Class - The field that represents this Ballerina object, which is used for Java subtyping.
# + _Serializable - The field that represents the superclass object `Serializable`.
# + _Type - The field that represents the superclass object `Type`.
# + _AnnotatedElement - The field that represents the superclass object `AnnotatedElement`.
# + _Object - The field that represents the superclass object `Object`.
# + _GenericDeclaration - The field that represents the superclass object `GenericDeclaration`.
type Class object {

    *JObject;

    ClassT _Class = ClassT;
    SerializableT _Serializable = SerializableT;
    TypeT _Type = TypeT;
    AnnotatedElementT _AnnotatedElement = AnnotatedElementT;
    ObjectT _Object = ObjectT;
    GenericDeclarationT _GenericDeclaration = GenericDeclarationT;

    # The init function of the Ballerina object mapping `java/lang/Class` Java class.
    #
    # + obj - The `handle` value containing the Java reference of the object.
    function init(handle obj) {
        self.jObj = obj;
    }

    # The function to retrieve the string value of a Ballerina object mapping a Java class.
    #
    # + return - The `string` form of the object instance.
    function toString() returns string {
        return jObjToString(self.jObj);
    }
};


