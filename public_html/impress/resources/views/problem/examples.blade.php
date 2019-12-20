{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')
@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">Problem examples per difficulty</div>

                <div class="panel-body">
                    @if (session('status'))
                        <div class="alert alert-success">
                            {{ session('status') }}
                        </div>
                    @endif
                <a href={{URL::previous()}} class="list-group-item">Go back</a> 
                <br />
                @for($i = 1; $i < 6; $i++)
                    <div class="panel panel-default">
                        <div class="panel-heading">Difficulty {{$i}}</div>
                        <div class="panel-body">
                            @if($examples[$i] !== null)
                            <ul class="list-group">
                                <li class="list-group-item">
                                    <span class="badge">{{$examples[$i]->header}}</span>
                                    Header:
                                </li>
                                <li class="list-group-item">
                                    <span class="badge">{{$examples[$i]->description}}</span>
                                    Description:
                                </li>
                                <li class="list-group-item">
                                    <span class="badge">{{$examples[$i]->pre_conditions}}</span>
                                    Preconditions:
                                </li>
                                <li class="list-group-item">
                                    <span class="badge">{{$examples[$i]->post_conditions}}</span>
                                    Postconditions:
                                </li>
                            </ul>
                            @else
                            There is no example for this difficulty available in our problem repository.
                            @endif
                        </div>
                    </div>
                @endfor

                </div>

                </div>
            </div>
        </div>
    </div>
</div>
@endsection
