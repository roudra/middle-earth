from django.shortcuts import render


# Create your views here.

def returnTripsPage(request):
    return render(request, 'tripViewer/trips.html')
