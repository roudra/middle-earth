from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse


def returnTrips(request):
    # return HttpResponse("Welcome to Trips Page")
    return render(request, 'tripview/trips.html')
def returnTrip(request, tripId):
    return render(request, 'tripview/trip.html', {'tripId': tripId})
