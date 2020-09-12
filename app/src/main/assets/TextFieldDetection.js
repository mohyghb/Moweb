document.addEventListener('click',function (f) {
   // if (f.type === 'text'){
        let e = f.target;
        jsInput.onReturn(e.id,e.innerText,e.value,e.type);
    //}
})