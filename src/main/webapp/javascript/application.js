$(function () {
    "use strict";

    var header = $('#header');
    var content = $('#content');
    var status = $('#status');
    var socket = atmosphere;
    var subSocket;
    var transport = 'long-polling';
    var fallbackTransport = 'long-polling';
    var sessionId;

    var isFirefox = typeof InstallTrigger !== 'undefined';
    var isChrome = !!window.chrome;

    if (isFirefox) {
        sessionId = 0;
    }
    if (isChrome) {
        sessionId = 1;
    }

    // We are now ready to cut the request
    var request = { url: document.location.toString() + 'event',
        contentType : "application/json",
        logLevel : 'debug',
        transport : transport,
        fallbackTransport : fallbackTransport ,
        trackMessageLength : true,
        reconnectInterval : 5000,
        enableXDR: true,
        timeout : 60000,
        headers : {sessionId : sessionId}
    };


    request.onOpen = function(response) {
        content.html($('<p>', { text: 'Atmosphere connected using ' + response.transport }));

        status.text('Connected with sessionId ' + sessionId);
        transport = response.transport;

        // Carry the UUID. This is required if you want to call subscribe(request) again.
        request.uuid = response.request.uuid;
    };

    request.onClientTimeout = function(r) {
        content.html($('<p>', { text: 'Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval }));
        subSocket.push(atmosphere.util.stringifyJSON({ message: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval }));
        setTimeout(function (){
            subSocket = socket.subscribe(request);
        }, request.reconnectInterval);
    };

    request.onReopen = function(response) {
        status.text('Connected with sessionId ' + sessionId);
        content.html($('<p>', { text: 'Atmosphere re-connected using ' + response.transport }));
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = atmosphere.util.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        var date = typeof(json.time) == 'string' ? parseInt(json.time) : json.time;
        addMessage(json.event, json.sessionId, new Date(date));
    };

    request.onClose = function(response) {
        content.html($('<p>', { text: 'Server closed the connection after a timeout' }));
        if (subSocket) {
            subSocket.push(atmosphere.util.stringifyJSON({ message: 'disconnecting' }));
        }
    };

    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your socket or the server is down' }));
    };

    request.onReconnect = function(request, response) {
        content.html($('<p>', { text: 'Connection lost, trying to reconnect. Trying to reconnect for ' + request.reconnectInterval + ' milliseconds'}));
        status.text('Connecting...');
    };

    subSocket = socket.subscribe(request);

    function addMessage(event, sessionId, datetime) {
        content.append('<p> @ ' +
            + (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
            + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
            + ' : ' + event + ' with sessionId:' + sessionId + '</p>');
    }
});
