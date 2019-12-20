{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">Invite new student to {{$room->name}}</div>

                <div class="panel body">
                    @if ($room->linkinvite)
                    {{-- Show the invite link if we have link invite: --}}
                    <div class="panel panel-default">
                        <div class="panel-heading"> You can invite a user by having them click the following link:</div>
                        <div class="panel-body">{{route('acceptlink', ['id' => $room->url])}}</div>
                    </div>
                    @else
                    {{-- Show the invite form if we have email invite: --}}
                    <form action="{{ route('room.invite', ['id' => $room->url]) }}" method="post">
                        {{csrf_field()}}
                        @if (Session::has('success'))
                            <div class="alert alert-success">
                            <ul>
                            @foreach (Session::get('success') as $msg)
                            <li>{{ $msg }}</li>
                            @endforeach
                            </ul>
                            </div>
                        @endif
                        <div class="form-group{{ $errors->has('email') ? ' has-error' : '' }}">
                            <label for="header" class="col-md-4 control-label">Student email address</label>

                            <div class="col-md-6">
                                <input id="email" type="text" class="form-control" name="email" value="{{ old('email') }}" required autofocus>

                                @if ($errors->has('email'))
                                    <span class="help-block">
                                        <strong>{{ $errors->first('email') }}</strong>
                                    </span>
                                @endif
                            </div>
                        </div>
                        <button type="submit">Send invite</button>
                    </form>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
