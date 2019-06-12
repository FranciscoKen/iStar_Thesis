const url = 'https://blooming-escarpment-99701.herokuapp.com/istar-service/validate';

const form = document.querySelector('form');
const successAlert = document.getElementById('success-alert');
const failAlert = document.getElementById('fail-alert');
var errorMessage = document.getElementById('error-message');

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
      successAlert.style.display='block';
    } else {
      response.json().then(json =>{
        console.log(json.error)
        errorMessage.textContent=json.error;
        failAlert.style.display='block';
      })
    }
    

  })
});