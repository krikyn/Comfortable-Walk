var autocompleteFrom, autocompleteTo, directionsService, directionsDisplay;

function initialize() {
    initMap();
    initAutocomplete();
}

function initMap() {
    directionsService = new google.maps.DirectionsService;
    directionsDisplay = new google.maps.DirectionsRenderer;
    var map = new google.maps.Map(document.getElementById("googleMap"), {
        center: {lat: 59.946079, lng: 30.318713},
        zoom:10,
    });
    directionsDisplay.setMap(map);

    
}

function initAutocomplete() {
    var defaultBounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(60.02781, 30.18035),
        new google.maps.LatLng(59.84802, 30.52101));
    var options = {
        bounds: defaultBounds,
        types: ['geocode'],
        componentRestrictions: {country: 'ru'}
    };
    autocompleteFrom = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteFrom')), options);
    autocompleteFrom.addListener('place_changed', fillInAddressFrom);

    autocompleteTo = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteTo')), options);
    autocompleteTo.addListener('place_changed', fillInAddressTo);
}

function fillInAddressFrom() {
    var placeFrom = autocompleteFrom.getPlace();
    document.getElementById('fromPointLat').value = placeFrom.geometry.location.lat(0);
    document.getElementById('fromPointLng').value = placeFrom.geometry.location.lng(0);
}

function fillInAddressTo() {
    var placeTo = autocompleteTo.getPlace();
    document.getElementById('toPointLat').value = placeTo.geometry.location.lat(0);
    document.getElementById('toPointLng').value = placeTo.geometry.location.lng(0);
}

function loading() {
    var fromPoint = document.getElementById('autocompleteFrom').value;
    var toPoint = document.getElementById('autocompleteTo').value;
    if (fromPoint !== "" && toPoint !== "")
        document.getElementsByClassName('loader')[0].style.visibility = "visible";
}

function calculateAndDisplayRoute(directionsService, directionsDisplay) {
    var originPoint = document.getElementById('fromPointLat').value + ", " + document.getElementById('fromPointLng').value;
    var destinationPoint = document.getElementById('toPointLat').value + ", " + document.getElementById('toPointLng').value;
    console.info(originPoint);
    directionsService.route({
        origin: originPoint,
        destination: destinationPoint,
        travelMode: 'WALKING'
    }, function(response, status) {
        if (status === 'OK') {
            directionsDisplay.setDirections(response);
        } else {
            window.alert('Directions request failed due to ' + status);
        }
    });
}