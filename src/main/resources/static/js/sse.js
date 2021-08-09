function sendSseRequest() {
    var eventSource = new EventSource("/sse-mvc?operationId=" + Math.ceil(Math.random() * 1_000_000));
    eventSource.onmessage = function (event) {
        $('input.sse-mvc').val(event.data)
    };
    eventSource.onerror = function(event){
        console.log(event + ' error')
        this.close()
    };
}

function getEvents() {
    $.get("/getEventList", function (events) {
        events.forEach(function (operationId) {
            var eventSource = new EventSource("/getEvent?operationId=" + operationId);
            eventSource.onmessage = function (event) {
                var textarea = $('textarea.list')
                textarea.append(event.data + "&#13;&#10;")
                this.close()
            };
            eventSource.onerror = function(event){
                console.log(operationId + ' error')
                this.close()
            };
            eventSource.addEventListener("test_event", function (event) {
                var textarea = $('textarea.list')
                textarea.append(event.data + "&#13;&#10;")
                this.close()
            })
        })
    });
}

function createEvents() {
    $.get("/createEvents")
}

function sendEvents() {
    $.get("/sendEvents")
}

function sendSseFluxRequest() {
    var eventSource = new EventSource("/sse-flux");
    eventSource.onmessage = function (event) {
        $('input.sse-flux').val(event.data)
        this.close()
    };
}

function sendFluxRequest() {
    var eventSource = new EventSource("/flux");
    eventSource.onmessage = function (event) {
        $('input.flux').val(event.data)
        this.close()
    };
}
