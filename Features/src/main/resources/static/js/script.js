let autocompleteFrom, autocompleteTo, directionsService, directionsDisplay;
let points = [];

function initialize() {
    initAutocompleteFrom();
    initAutocompleteTo();
    initMap();
}

function initAutocompleteFrom() {
    const defaultBounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(60.02781, 30.18035),
        new google.maps.LatLng(59.84802, 30.52101));
    const options = {
        bounds: defaultBounds,
        types: ['geocode'],
        componentRestrictions: {country: 'ru'}
    };
    autocompleteFrom = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteFrom')), options);
    autocompleteFrom.addListener('place_changed', function () {
        const placeFrom = autocompleteFrom.getPlace();
        document.getElementById('fromPointLat').value = placeFrom.geometry.location.lat(0);
        document.getElementById('fromPointLng').value = placeFrom.geometry.location.lng(0);

    });
}

function initAutocompleteTo() {
    const defaultBounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(60.02781, 30.18035),
        new google.maps.LatLng(59.84802, 30.52101));
    const options = {
        bounds: defaultBounds,
        types: ['geocode'],
        componentRestrictions: {country: 'ru'}
    };
    autocompleteTo = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteTo')), options);
    autocompleteTo.addListener('place_changed', function () {
        const placeTo = autocompleteTo.getPlace();
        document.getElementById('toPointLat').value = placeTo.geometry.location.lat(0);
        document.getElementById('toPointLng').value = placeTo.geometry.location.lng(0);
    });
}

function loading() {
    const fromPoint = document.getElementById('autocompleteFrom').value;
    const toPoint = document.getElementById('autocompleteTo').value;
    if (fromPoint !== "" && toPoint !== "")
        document.getElementsByClassName('loader')[0].style.visibility = "visible";
}

function sendData() {
    $("button").click(function (event) {
        //stop submit the form, we will post it manually.
        event.preventDefault();
        event.stopImmediatePropagation();
        let places = "";
        for (let i = 0; i < document.getElementsByName('placeName').length; i++) {
            places = document.getElementsByName('placeName')[i].value + "," + places;
        }
        let search = {};
        search["fromPointLng"] = document.getElementById('fromPointLng').value;
        search["fromPointLat"] = document.getElementById('fromPointLat').value;
        search["toPointLng"] = document.getElementById('toPointLng').value;
        search["toPointLat"] = document.getElementById('toPointLat').value;
        search["placeName"] = places;
        search["isBestWeather"] = document.getElementById('weather').checked;
        console.log(search);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "http://localhost:9091/build",
            data: JSON.stringify(search),
            dataType: 'json',
            cache: false,
            // timeout: 600000,
            beforeSend: loading(),
            success: function (data) {
                document.getElementsByClassName('loader')[0].style.visibility = "hidden";
                console.log(data);
                calculateAndDisplayRoute(data);
            }
        });
        return false;
    });
}

function initMap() {
    directionsService = new google.maps.DirectionsService;
    directionsDisplay = new google.maps.DirectionsRenderer;
    const map = new google.maps.Map(document.getElementById("googleMap"), {
        center: {lat: 59.946079, lng: 30.318713},
        zoom: 12,
    });
    directionsDisplay.setMap(map);
}

function calculateAndDisplayRoute(data) {
    const map = new google.maps.Map(document.getElementById("googleMap"), {
        center: {lat: 59.946079, lng: 30.318713},
        zoom: 12,
    });
    var shapes = [];
    var path = [];
    for (var i = 0; i < data.length; i++) {
        path.push(new google.maps.LatLng(data[i][0], data[i][1]))
    }
    var polyline = new google.maps.Polyline({path: path, strokeColor: "#FF0000", strokeOpacity: 1.0, strokeWeight: 2});
    polyline.setMap(map);
    map.setCenter(new google.maps.LatLng(59.94725871655315, 30.303347840670995), 13);
    shapes.push(polyline);
}

