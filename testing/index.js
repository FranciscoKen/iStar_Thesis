var fs = require('fs');
var axios = require('axios');

const axiosConfig = {
    headers : {
        'Content-Type': 'multipart/form-data'
    }
};

function readFiles(dirname,onFileContent,onError){
    var i = 0;
    fs.readdirSync(dirname,function(err,filenames){
        if(err){
            onError(err);
            return;
        }
        filenames.forEach(function(filename){
            fs.readFileSync(dirname + filename, 'utf-8',function(err,content){
                if(err){
                    onError(err);
                    return;
                }
                onFileContent(filename,content);
            });
        });
    });
}

const validate_post = (content) => {
    try {
        return axios.post('http://blooming-escarpment-99701.herokuapp.com/istar-service/validate',content,axiosConfig);
    } catch (error){
        console.log(error);
    }
}

const validate = async (content) => {
    const validation_response = validate_post(content)
    .then(response => {
        console.log(response);
    }).catch(error => {
        console.log(error);
    })
}

var data = {};
readFiles('../testcases/iStar2.0/',function(filename,content){
    data[filename] = content;
    validate(content);
},function(err){
    throw err;
});