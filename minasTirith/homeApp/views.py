from django.shortcuts import render


# Create your views here.

def returnHomePage(request):
    return render(request, 'home.html')
