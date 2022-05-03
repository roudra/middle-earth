from django.shortcuts import render
from django.views.generic import ListView
from django_tables2 import SingleTableView
from django_filters.views import FilterView
from django_tables2.views import SingleTableMixin
from django.http import JsonResponse
import django_tables2 as tables
from .models import Trip
from django.db.models import Count

from .models import Trip
from .forms import TripForm

# Create your views here.
class TripTable(tables.Table):
    class Meta:
        model = Trip
        template_name = "django_tables2/bootstrap.html"
        sequence = ('rideId',)

class TripListView(SingleTableView):
    table_class = TripTable
    queryset = Trip.objects.all()
    template_name = 'tripViewer/trips.html'

def chart(request):
    labels = []
    data = []
    q = Trip.objects.values('memberType').annotate(Count('memberType'))
    for row in q:
        labels.append(row['memberType'])
        data.append(row['memberType__count'])
    return render(request, 'tripViewer/reports.html', {
        'pie_labels': labels,
        'pie_data': data,
    })

def reports(request):
    return render(request, 'tripViewer/reports.html')

# def returnTripsPage(request):
#     trips = Trip.objects.all()
#     context = {
#         'trips': trips
#     }
#     return render(request, 'tripViewer/trips.html', context)
# class TripListView(ListView):
#     model = Trip
#     table_class = TripTable
#     template_name = 'tripViewer/trips.html'



def returnAddTripsPage(request):
    form = TripForm()
    context = {"form": form}
    if request.method == 'POST':
        form = TripForm(request.POST)
        print(form)
        print(form.is_valid())
        if form.is_valid():
            form.save()
            return redirect('trips')
    return render(request, 'tripViewer/trip_details.html', context)


