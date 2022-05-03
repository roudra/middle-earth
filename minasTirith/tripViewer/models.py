from django.db import models


# Create your models here.
class Trip(models.Model):
    rideId = models.CharField(null=False, blank=False, unique=True, primary_key=True, max_length=256, db_column='rideid')
    rideableType = models.CharField(null=True, max_length=256, db_column='rideabletype')
    startedAt = models.DateTimeField(null=True, db_column='startedat')
    endedAt = models.DateTimeField(null=True, db_column='endedat')

    startStationName=models.CharField(null=True, max_length=256, db_column='startstationname')
    startStationId=models.CharField(null=True, max_length=256, db_column='startstationid')
    endStationName=models.CharField(null=True, max_length=256, db_column='endstationname')
    endStationId=models.CharField(null=True, max_length=256, db_column='endstationid')

    startLat=models.FloatField(null=True, db_column='startlat')
    startLong=models.FloatField(null=True, db_column='startlong')
    endLat=models.FloatField(null=True, db_column='endlat')
    endLong=models.FloatField(null=True, db_column='endlong')
    memberType=models.CharField(null=True, max_length=256, db_column='membertype')

    class Meta:
        db_table = "trip"

    def __str__(self):
        return self.rideId
