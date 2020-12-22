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