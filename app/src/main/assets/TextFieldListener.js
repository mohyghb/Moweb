//document.addEventListener('input', function (f) {
//    let e = f.target;
//    let tagName = e.tagName;
//    jsInput.onClicked(e.id,e.name,e.type);
//});

document.addEventListener('click',function (f) {
        let e = f.target;
        let tagName = e.tagName;
        if (tagName === 'INPUT') {
             jsInput.onClicked(e.id,e.name,e.type);
        }
//        else if(tagName === 'DIV' || tagName === 'BUTTON'){
//               // get all the text field forms values and everything
//                jsInput.onReturn($('form').serialize(),"","","");
//        } else {
//            jsInput.printType(tagName,e.type);
//        }
});