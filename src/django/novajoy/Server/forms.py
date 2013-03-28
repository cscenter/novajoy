from django import forms
from captcha.fields import CaptchaField
from django.utils.translation import ugettext_lazy as _
from registration.forms import RegistrationForm
class CustomRegForm(RegistrationForm):
    age=forms.IntegerField(
        label=_('age'),
        )
    captcha = CaptchaField(label=_("Code in the image"))
