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


def chart_end_stations(request):
    labels = []
    data = []
    queryset = Trip.objects.values('endStationName').annotate(Count('endStationName')).order_by(
        '-endStationName__count')
    for entry in queryset:
        labels.append(entry['endStationName'])
        data.append(entry['endStationName__count'])
    return JsonResponse(data={'labels': labels,'data': data,})

def chart_members(request):
    labels = []
    data = []
    queryset = Trip.objects.values('memberType').annotate(Count('memberType'))
    for entry in queryset:
        labels.append(entry['memberType'])
        data.append(entry['memberType__count'])
    return JsonResponse(data={'labels': labels,'data': data,})
