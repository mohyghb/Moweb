//document.addEventListener('input', function (f) {
//    let e = f.target;
//    let tagName = e.tagName;
//    jsInput.onClicked(e.id,e.name,e.type);
//});
let ranAlready = false;

if(!ranAlready) {
    document.addEventListener('click',function (f) {
            let e = f.target;
            let tagName = e.tagName;
            if (tagName === 'INPUT') {
                 jsInput.onClicked(e.id,e.name,e.type);
//                 if(e.hasAttributes()) {
//                      var attrs = e.attributes;
//                      var output = "";
//                      for(var i = attrs.length - 1; i >= 0; i--) {
//                        output += attrs[i].name + "->" + attrs[i].value + "\n";
//                      }
//                      jsInput.print(output);
//                 }else{
//                    jsInput.print("no attributes");
//                 }
            }
    });
    ranAlready = true;
    jsInput.print("run now");
}
