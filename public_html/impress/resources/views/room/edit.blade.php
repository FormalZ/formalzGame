{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">Edit room {{$room->name}}</div>

                <div class="panel-body">
                    <form class="form-horizontal" method="POST" action="{{ route('room.edit', ['id' => $room->url]) }}">
                        {{ csrf_field() }}

                        <div class="form-group{{ $errors->has('name') ? ' has-error' : '' }}">
                            <label for="name" class="col-md-4 control-label">New Name</label>

                            <div class="col-md-6">
                                <input id="name" type="text" class="form-control" name="name" value="{{$room->name}}" required autofocus>

                                @if ($errors->has('name'))
                                    <span class="help-block">
                                        <strong>{{ $errors->first('name') }}</strong>
                                    </span>
                                @endif
                            </div>
                        </div>

                        <div class="form-group{{ $errors->has('description') ? ' has-error' : '' }}">
                            <label for="description" class="col-md-4 control-label">New Description</label>

                            <div class="col-md-6">
                                <input id="description" type="text" class="form-control" name="description" value="{{$room->description}}" required>

                                @if ($errors->has('description'))
                                    <span class="help-block">
                                        <strong>{{ $errors->first('description') }}</strong>
                                    </span>
                                @endif
                            </div>
                        </div>

                        <div class="form-group{{ $errors->has('linkinvite') ? ' has-error' : '' }}">
                            <label for="linkinvite" class="col-md-4 control-label">Should the room have an invitation link? (Uncheck for email-only invitations)</label>

                            <div class="col-md-6">
                                @if ($room->linkinvite)
                                    <input id="linkinvite" type="checkbox" class="form-control" name="linkinvite" value="linkinvite" checked>
                                @else
                                    <input id="linkinvite" type="checkbox" class="form-control" name="linkinvite" value="linkinvite">
                                @endif

                                @if ($errors->has('linkinvite'))
                                    <span class="help-block">
                                        <strong>{{ $errors->first('linkinvite') }}</strong>
                                    </span>
                                @endif
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-6 col-md-offset-4">
                                <button type="submit" class="btn btn-primary">
                                    Save Changes
                                </button>
                                <button type="button" onclick="window.location.href='/room/{{$room->url}}/delete'">
                                    Delete this room
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
