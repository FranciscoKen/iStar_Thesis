var xsd = require('libxml-xsd');

var schemaPath = '../../../standards/alternative.xsd';
var documentPath = '../../test.xml';
xsd.parseFile(schemaPath, function(err, schema){
  schema.validateFile(documentPath, function(err, validationErrors){
    // err contains any technical error
    // validationError is an array, null if the validation is ok
    console.log("err :"+err);
    console.log("validation errors: "+validationErrors);
  });  
});