var stompClient = null;

function connect() {
    stompClient = Stomp.client('ws://localhost:8080/ws');
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/greetings', function (response) {
            showGreeting(JSON.parse(response.body).content);
        });
    });
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    var textarea = $('textarea.list');
    textarea.append(message + "&#13;&#10;");
}