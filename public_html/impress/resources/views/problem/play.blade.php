{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.game')
@section('content')
<form>
<input type="hidden" id="token" value="{{$token}}">
<input type="hidden" id="problemroute" value="{{$problemroute}}">
</form>
@endsection
@section('pagescript')
<script type="text/javascript" src="/js/formalz-config.js"></script>
<script type="text/javascript" src="/js/game.min.js"></script>
@endsection
