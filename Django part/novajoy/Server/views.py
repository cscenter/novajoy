from django.http import HttpResponse

def mainPage(request):
   return HttpResponse("Hello world")
