{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">Do you really want to delete {{$room->name}}?</div>

                <div class="panel-body">
                    <form class="form-horizontal" method="POST" action="{{ route('room.delete', ['id' => $room->url]) }}">
                        {{ csrf_field() }}
                            <div class="col-md-6 col-md-offset-4">
                            	<button type="button" onclick="window.location.href='/room/{{$room->url}}'">
                                    Cancel
                                </button>
                                <button type="submit" class="btn btn-primary" color="FF0000">
                                    Delete room!
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
