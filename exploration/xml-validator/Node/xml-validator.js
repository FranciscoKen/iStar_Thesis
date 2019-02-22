var xsd = require('libxml-xsd');

var schemaPath = "../../../standards/alternative.xsd";
var documentPath = "../../../testcases/university-travel-default-istar2.xml";

xsd.parseFile(schemaPath, function(err, schema){
  console.log("Err 1:"+err);
  schema.validateFile(documentPath, function(err, validationErrors){
    // err contains any technical error
    // validationError is an array, null if the validation is ok
    console.log("err :"+err);
    console.log("validation errors: "+validationErrors);
  });  
});