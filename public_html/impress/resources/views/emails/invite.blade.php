{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
<p>Hi,</p>
@if ($invite->invitetype == 'teacher')
    <p>The admin has invited you to create a new teacher account on the Ludiscite server.</p>
@else
    <p>A teacher has invited you to create an account in the {{$invite->roomname}} room.</p>
@endif
<a href="{{ route('acceptmail', $invite->token) }}">Click here</a> to create the account!
<br/>
<br/>
Kind regards,
<br/>
Ludiscite.
