from django.db import models

class Trip(models.Model):
    rideId = models.CharField(max_length=100)
    rideableType = models.CharField(max_length=100)
    startedAt = models.DateTimeField(auto_now_add=True)
    endedAt = models.DateTimeField(auto_now_add=True)
    startStationName = models.CharField(max_length=100)
    startStationId = models.CharField(max_length=100)
    endStationName = models.CharField(max_length=100)
    endStationId = models.CharField(max_length=100)
    startLat = models.CharField(max_length=100)
    startLong = models.CharField(max_length=100)
    endLat = models.CharField(max_length=100)
    endLong = models.CharField(max_length=100)
    memberType = models.CharField(max_length=100)

    def __str__(self):
        return self.rideId