{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">{{$room->name}}</div>

                    <div class="panel-body">
                        @if (session('status'))
                            <div class="alert alert-success">
                                {{ session('status') }}
                            </div>
                        @endif

                        {{$room->description}}
                        <br/><br/><br/>
                        @if ($iscreator)
                            <div class="well">
                                <button onclick="window.location.href='/room/{{$room->url}}/edit'">Edit Room</button>
                                <button onclick="window.location.href='/room/{{$room->url}}/createproblem'">Add new
                                    problem
                                </button>
                                <button onclick="window.location.href='/room/{{$room->url}}/invite'">Invite students
                                </button>
                            </div>
                            @if ($archive != [])
                                <ul class="nav nav-tabs">
                                    <li class="active"><a data-toggle="tab" href="#problems">Active Problems</a>
                                    </li>
                                    <li><a data-toggle="tab" href="#archive">Archived Problems</a></li>
                                </ul>
                            @endif
                            <div class="tab-content">
                                <div id="problems" class="tab-pane fade in active">
                                    @endif
                                    <div class="panel panel-default">
                                        <div class="panel-heading">Problems in this room:</div>
                                        <div class="panel-body">
                                            @foreach($problems as $p)
                                                <li class="list-group-item">
                                                    <a href="{{route('problem.show', ['id' => $p->id])}}">
                                                        @if ($p->hide)
                                                            HIDDEN -
                                                        @endif
                                                        {{$p->header}}
                                                    </a>
                                                </li>
                                            @endforeach
                                        </div>
                                    </div>
                                    @if ($iscreator)
                                        <div class="panel panel-default">
                                            <div class="panel-heading">Users in this room:</div>
                                            <div class="panel-body">
                                                @foreach($users as $u)
                                                    <li class="list-group-item">
                                                        {{$u}}
                                                    </li>
                                                @endforeach
                                            </div>
                                        </div>
                                </div>
                                @if ($archive != [])
                                    <div id="archive" class="tab-pane fad">
                                        <div class="panel panel-default">
                                            <div class="panel-heading">Archived problems in this room:</div>
                                            <div class="panel-body">
                                                @foreach($archive as $p)
                                                    <li class="list-group-item">
                                                        <a href="{{route('problem.show', ['id' => $p->id])}}">
                                                            {{$p->header}}
                                                        </a>
                                                    </li>
                                                @endforeach
                                            </div>
                                        </div>
                                    </div>
                                @endif
                                @endif
                            </div>
                    </div>
                </div>
            </div>
        </div>
@endsection
