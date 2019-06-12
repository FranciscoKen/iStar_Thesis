const url = 'https://blooming-escarpment-99701.herokuapp.com/istar-service/class-diagram';
var image_url = 'https://blooming-escarpment-99701.herokuapp.com/istar-service/class-image';
const ocl_url = 'https://blooming-escarpment-99701.herokuapp.com/istar-service/class-ocl';

const form = document.querySelector('form');
const successAlert = document.getElementById('success-alert');
const failAlert = document.getElementById('fail-alert');
var errorMessage = document.getElementById('error-message');
var outside;
const image_result = document.getElementById('class-result');
const ocl_paragraph = document.getElementById('ocl_result');

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
    }
  }).then(response=>{
    if(response.ok){
      console.log(response.status);
      response.json().then(json=>{
        console.log(json.uid);
        const uid = json.uid;
        const headers = new Headers({});
        var options = {
          method: 'GET',
          headers: headers,
          // mode: 'cors',
          cache: 'default',
          'Access-Control-Allow-Origin':'*'
        };


        var outside
        fetch(image_url+'?uid='+uid,options)
        .then(img_response => img_response.blob())
        .then(images => {
            outside = URL.createObjectURL(images)
            image_result.src=outside;
            image_result.style.display='inline-block';
            successAlert.style.display='block';
        })

        fetch(ocl_url+'?uid='+uid,options)
        .then(ocl_response=>ocl_response.text())
        .then(text=>{
          console.log(text);
          ocl_paragraph.innerText = text;
          ocl_paragraph.style.display ='inline-block';
          successAlert.style.display='block';
        })

      });
    }
  })
});

function arrayBufferToBase64(buffer) {
  var binary = '';
  var bytes = [].slice.call(new Uint8Array(buffer));

  bytes.forEach((b) => binary += String.fromCharCode(b));

  return window.btoa(binary);
};