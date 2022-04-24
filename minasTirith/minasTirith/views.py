from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse

def returnHomePage(request):
    return render(request, 'home.html')
def returnDevPage(request):
    return render(request, 'profile.html')