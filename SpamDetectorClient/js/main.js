// TODO: onload function should retrieve the data needed to populate the UI

let urlAPIData = "http://localhost:8080/spamDetector-1.0/api/spam";
let urlAPIAccuracy = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy";
let urlAPIPrecision = "http://localhost:8080/spamDetector-1.0/api/spam/precision";

function loadTable(response) {
  console.log("Loaded data from " + urlAPIData);
  console.log(response);
  const tableBody = document.getElementById("body");
  for(let c of response){
    var newRow = tableBody.insertRow(-1);
    var fileCell = newRow.insertCell(0);
    var spamProbCell = newRow.insertCell(1);
    var guessCell = newRow.insertCell(2);
    var classCell = newRow.insertCell(3);
    fileCell.innerHTML = c.filename;
    spamProbCell.innerHTML = c.spamProbability;
    guessCell.innerHTML = c.guessClass;
    classCell.innerHTML = c.actualClass;
    /*
    elementId.innerHTML+="<tr><td>" + c.filename + "</td><td>" +
      c.spamProbability + "</td><td>" + c.guessClass + "</td><td>" +
      c.actualClass + "</td></tr>";*/
  }
}
function loadAccuracy(response) {
  console.log("Loaded data from " + urlAPIAccuracy);
  console.log(response);
  const accuracyElement = document.getElementById("accuracy");
  accuracyElement.innerHTML += "<p><span>" + response + "</span></p>";
}
function loadPrecision(response) {
  console.log("Loaded data from " + urlAPIPrecision);
  console.log(response);
  const precisionElement = document.getElementById("precision");
  precisionElement.innerHTML += "<p><span>" + response + "</span></p>";
}
function requestDataFromServer(url,func){
  fetch(url,{
    method: "GET",
    headers:{
      "Accept": "application/json"
    }
  }).then(response => response.json())
    .then(response => func(response))
    .catch((err) => {
      console.log("Something went wrong: " + err);
    });
}
(function(){
  requestDataFromServer(urlAPIData,loadTable);
  requestDataFromServer(urlAPIAccuracy,loadAccuracy);
  requestDataFromServer(urlAPIPrecision,loadPrecision);
})();


