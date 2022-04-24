from django.db import models


# Create your models here.
class Trip(models.Model):
    rideId = models.CharField(null=False, blank=False, unique=True, primary_key=True, max_length=1000)
    rideableType = models.CharField(null=False, max_length=1000)
    startedAt = models.DateTimeField(null=False)

    def __str__(self):
        return self.rideId
