var placeSearch, autocompleteFrom, autocompleteTo;

function initAutocomplete() {
    autocompleteFrom = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteFrom')),
        {types: ['geocode']});
    autocompleteFrom.addListener('place_changed', fillInAddress);
    autocompleteTo = new google.maps.places.Autocomplete(
        (document.getElementById('autocompleteTo')),
        {types: ['geocode']});
    autocompleteTo.addListener('place_changed', fillInAddress);
}

function fillInAddress() {
    // Get the place details from the autocompleteFrom object.
    var placeFrom = autocompleteFrom.getPlace();
    var placeTo = autocompleteTo.getPlace();

    document.getElementById('fromPointLat').value = placeFrom.geometry.location.lat(0);
    document.getElementById('fromPointLng').value = placeFrom.geometry.location.lng(0);
    document.getElementById('toPointLat').value = placeTo.geometry.location.lat(0);
    document.getElementById('toPointLng').value = placeTo.geometry.location.lng(0);
}

function geolocate() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            var geolocation = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            var circle = new google.maps.Circle({
                center: geolocation,
                radius: position.coords.accuracy
            });
            autocompleteFrom.setBounds(circle.getBounds());
            autocompleteTo.setBounds(circle.getBounds());
        });
    }
}