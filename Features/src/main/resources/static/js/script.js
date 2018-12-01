var autocompleteFrom, autocompleteTo;

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