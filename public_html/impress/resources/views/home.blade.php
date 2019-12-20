{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">Dashboard</div>

                <div class="panel-body">
                    @if (session('status'))
                        <div class="alert alert-success">
                            {{ session('status') }}
                        </div>
                    @endif

                    Welcome to the dashboard.
                    <ul class="nav nav-tabs">
                        <li class="active"><a data-toggle="tab" href="#roomsstudent">Rooms that you are a student in</a></li>
                        @if ($isteacher)
                        <li><a data-toggle="tab" href="#roomsteacher">Rooms that you are a teacher in</a></li>
                        @endif
                        <li><a data-toggle="tab" href="#accountsettings">Account settings</a></li>
                    </ul>
                    <div class="tab-content">
                    
                    <div id="roomsstudent" class="tab-pane fade in active">
                        @foreach($userrooms as $r)
			<li class="list-group-item">
			    <a href="{{route('room.show', ['id' => $r->url])}}">
				{{$r->name}}
		            </a>
			</li>
		        @endforeach
                    </div>

		    @if ($isteacher)
                    <div id="roomsteacher" class="tab-pane fade">
			@foreach($teacherrooms as $r)
			<li class="list-group-item">
			    <a href="{{route('room.show', ['id' => $r->url])}}">
				{{$r->name}}
			    </a>
			</li>
			@endforeach
                    </div>
		    @endif

                    <div id="accountsettings" class="tab-pane fade">
                        <li class="list-group-item">
                            <a href="{{route('user.changepassword')}}">
                                Change your Password
                            </a>
                        </li>
                    </div>
                    </div>

                    <br />
                    <br />
                    @if($isteacher)
	            <button onclick="window.location.href='/createroom'">Create a new room</button>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
