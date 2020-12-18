//document.addEventListener('click',function (f) {
//        let e = f.target;
//        let tagName = e.tagName;
//        if (tagName === 'INPUT') {
//                jsInput.onReturn(e.id,e.innerText,e.value,e.tagName);
//        } else if(tagName === 'DIV' || tagName === 'BUTTON'){
//               // get all the text field forms values and everything
//                jsInput.onReturn($('form').serialize(),"","","");
//        } else {
//            jsInput.printType(tagName,e.type);
//        }
//});
//
//$(document).ready(function() {
//        jsInput.isWorking();
//});

//jsInput.onBeginListening();
//document.addEventListener('input', function (f) {
//    let e = f.target;
//    let tagName = e.tagName;
//    // monote testing name instead of id for saving it inside auto fill
//    jsInput.onReturn(e.id,e.value,e.type);
//});
let run = false;

if(!run) {
    let inputs = document.getElementsByTagName('input');
    for(const inp of inputs) {
        if(inp.type === 'text' || inp.type === 'password' || inp.type === 'email'){
            jsInput.onGather(inp.name,inp.id,inp.value,inp.type,inp.autocomplete);
        }
    }
    jsInput.onFinishedGathering();
    run = true;
}