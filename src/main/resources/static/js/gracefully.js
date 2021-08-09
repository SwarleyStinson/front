function doWork() {
    var eventSource = new EventSource("/doWork");
    eventSource.onmessage = function (event) {
        $('input.gracefully-work').val(event.data);
        this.close();
    };
    eventSource.onerror = function(event){
        console.log(event + ' error')
        this.close()
    };
}


