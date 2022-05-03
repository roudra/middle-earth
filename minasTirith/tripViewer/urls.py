"""homeApp URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from . import views
from . import apis
from .views import TripListView

urlpatterns = [
    # path('', views.returnTripsPage, name="trips"),
    path('', TripListView.as_view(), name="trips"),
    path('new', views.returnAddTripsPage, name="add_trip"),
    path('report',  views.reports, name="trip-reports"),

    path('api/stations', apis.chart_end_stations, name="api-stations-chart"),
    path('api/members', apis.chart_members, name="api-members-chart"),
    # path('/<str:pk>', views.returnEditTripsPage, name="trip_edit"),
]
