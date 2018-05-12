$(document).ready(function() {
    $(".date").each(function(index) {
        var dateEpoch = $(this).text();
        var dateHuman = new Date(dateEpoch*1000);
        console.log(dateHuman);
        $(this).html(dateHuman.toLocaleDateString("en-US") + "<span class=\"advert-time\"> " + dateHuman.toLocaleTimeString("en-US") + "</span>");
    });
});