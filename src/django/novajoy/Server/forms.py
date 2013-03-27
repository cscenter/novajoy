from django.conf import settings
from django import forms
from django.utils.translation import ugettext_lazy as _
from registration.forms import RegistrationForm

class CustomRegForm(RegistrationForm):
    age=forms.IntegerField(
        label=_('age'),
        )