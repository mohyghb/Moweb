let ranAlready = false;

if(!ranAlready) {
    document.addEventListener('click',function (f) {
            let e = f.target;
            let tagName = e.tagName;
            if (tagName === 'INPUT') {
                 jsInput.onClicked(e.id,e.name,e.type,e.autocomplete);
                 jsInput.print("value = " + e.value);
            }
    });
    ranAlready = true;
    jsInput.print("run now");
}


/*
    fills the user and password to their respective
    fields inside the web page if they exist
*/
function fillUserPass(userId,userVal,passId,passVal) {
    jsInput.print('filling user pass');
    bruteForceFill(userId,'username',userVal);
    bruteForceFill(passId,'current-password',passVal);
}


function bruteForceFill(idOrName, autocomplete, value) {
    let inputs = document.getElementsByTagName('input');
    for(const inp of inputs) {
        if(meetsReq(inp,autocomplete,idOrName)) {
            // then we can auto fill this input

            inp.value = value;
            jsInput.print(autocomplete + ' yay ' + inp.value);
            break;
        }
    }
}
/*
    returns true if the input parameter
    meets the requirements that are passed in as params
*/
function meetsReq(input, autocomplete, auto) {
    return  input.autocomplete === auto ||
            input.autocomplete === autocomplete ||
            autocomplete.includes(input.name);
}




