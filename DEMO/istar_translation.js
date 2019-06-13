const url = 'https://blooming-escarpment-99701.herokuapp.com/istar-service/convert-istarml';

const form = document.querySelector('form');
const successAlert = document.getElementById('success-alert');
const failAlert = document.getElementById('fail-alert');
var errorMessage = document.getElementById('error-message');
const istarml2_result = document.getElementById('istarml2');
const result_cont = document.getElementById('result_cont');


form.addEventListener('submit',e=>{
  e.preventDefault();
  const files = document.querySelector('[type=file][multiple]').files;
  const formData = new FormData();

  for(let i =0;i<files.length;i++){
    let file = files[i];
    formData.append('file',file);
  }

  fetch(url,{
    mode:'cors',
    method:'POST',
    body:formData,
    headers:{
      'Access-Control-Allow-Origin':'*'
    },
  })
  .then(response=>{
    if(response.ok){
      response.text()
      .then(text =>{
        successAlert.style.display='inline-block';
        console.log(text);
        var beautified_text = vkbeautify.xml(text);
        istarml2_result.innerText = beautified_text.replace("(<\\?xml(.*?)\\?>\n)","");
        istarml2_result.style.display ='inline-block';
        result_cont.style.display = 'inline-block';
        successAlert.style.display='block';
      });
      
    } else {
      response.text()
      .then(text => {
        console.log(text);
        errorMessage.textContent=text;
        failAlert.style.display='inline-block';
      });
      // response.json().then(json =>{
      //   console.log(json.error)
      //   errorMessage.textContent=json.error;
      //   failAlert.style.display='inline-block';
      // });
    }
  });
  // .then(response=>response.text())
  // .then(text=>{
  //   // if(response.ok){
  //     // console.log(response.status);
  //     successAlert.style.display='inline-block';
  //     console.log(text);
  //     var beautified_text = vkbeautify.xml(text);
  //     istarml2_result.innerText = beautified_text.replace("(<\\?xml(.*?)\\?>\n)","");
  //     istarml2_result.style.display ='inline-block';
  //     successAlert.style.display='block';

  //   // } else {
  //   //   response.json().then(json =>{
  //   //     console.log(json.error)
  //   //     errorMessage.textContent=json.error;
  //   //     failAlert.style.display='inline-block';
  //   //   })
  //   // }
  // })
});

function formatXML(input,indent)
{
  indent = indent || '\t'; //you can set/define other ident than tabs


  //PART 1: Add \n where necessary
  xmlString = input.replace(/^\s+|\s+$/g, '');  //trim it (just in case) {method trim() not working in IE8}

  xmlString = input
                   .replace( /(<([a-zA-Z]+\b)[^>]*>)(?!<\/\2>|[\w\s])/g, "$1\n" ) //add \n after tag if not followed by the closing tag of pair or text node
                   .replace( /(<\/[a-zA-Z]+[^>]*>)/g, "$1\n") //add \n after closing tag
                   .replace( />\s+(.+?)\s+<(?!\/)/g, ">\n$1\n<") //add \n between sets of angled brackets and text node between them
                   .replace( />(.+?)<([a-zA-Z])/g, ">\n$1\n<$2") //add \n between angled brackets and text node between them
                   .replace(/\?></, "?>\n<") //detect a header of XML

  xmlArr = xmlString.split('\n');  //split it into an array (for analise each line separately)



  //PART 2: indent each line appropriately

  var tabs = '';  //store the current indentation
  var start = 0;  //starting line

  if (/^<[?]xml/.test(xmlArr[0]))  start++;  //if the first line is a header, ignore it

  for (var i = start; i < xmlArr.length; i++) //for each line
  {  
    var line = xmlArr[i].replace(/^\s+|\s+$/g, '');  //trim it (just in case)

    if (/^<[/]/.test(line))  //if the line is a closing tag
     {
      tabs = tabs.replace(indent, '');  //remove one indent from the store
      xmlArr[i] = tabs + line;  //add the tabs at the beginning of the line
     }
     else if (/<.*>.*<\/.*>|<.*[^>]\/>/.test(line))  //if the line contains an entire node
     {
      //leave the store as is
      xmlArr[i] = tabs + line; //add the tabs at the beginning of the line
     }
     else if (/<.*>/.test(line)) //if the line starts with an opening tag and does not contain an entire node
     {
      xmlArr[i] = tabs + line;  //add the tabs at the beginning of the line
      tabs += indent;  //and add one indent to the store
     }
     else  //if the line contain a text node
     {
      xmlArr[i] = tabs + line;  // add the tabs at the beginning of the line
     }
  }


  //PART 3: return formatted string (source)
  return  xmlArr.join('\n');  //rejoin the array to a string and return it
}
